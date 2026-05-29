import { useTranslation } from 'react-i18next'
import { boundaryOptions, type BoundaryCode } from '../../game-session/model/gameSession'

type BoundaryPickerProps = {
  selected: BoundaryCode[]
  onChange: (boundaries: BoundaryCode[]) => void
  compact?: boolean
}

export function BoundaryPicker({ selected, onChange, compact = false }: BoundaryPickerProps) {
  const { t } = useTranslation()

  function toggle(boundary: BoundaryCode) {
    if (selected.includes(boundary)) {
      onChange(selected.filter((current) => current !== boundary))
      return
    }

    onChange([...selected, boundary])
  }

  return (
    <fieldset className={compact ? 'boundary-grid boundary-grid--compact' : 'boundary-grid'}>
      <legend>{t('boundaries.title')}</legend>
      {boundaryOptions.map((option) => (
        <label className="boundary-choice" key={option.value}>
          <input
            type="checkbox"
            checked={selected.includes(option.value)}
            onChange={() => toggle(option.value)}
          />
          <span>{t(`game.boundary.${option.value}`)}</span>
        </label>
      ))}
    </fieldset>
  )
}
